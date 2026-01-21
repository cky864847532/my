#!/usr/bin/env python3
import json, sys, os

before_path = 'api_test_results.json'
after_path = os.path.join('..','encoding_fix_report.json') if not os.path.exists('encoding_fix_report.json') else 'encoding_fix_report.json'
# allow override
if len(sys.argv) > 1:
    before_path = sys.argv[1]
if len(sys.argv) > 2:
    after_path = sys.argv[2]

if not os.path.exists(before_path):
    print('Before file not found:', before_path); sys.exit(2)
if not os.path.exists(after_path):
    print('After file not found:', after_path); sys.exit(2)

with open(before_path, 'r', encoding='utf-8', errors='replace') as f:
    before = json.load(f)
with open(after_path, 'r', encoding='utf-8', errors='replace') as f:
    after = json.load(f)

report = { 'products': [], 'orders': [], 'inventory_records': [], 'ai_chats': [] }

# helper
def normalize_record(obj):
    if not isinstance(obj, dict): return obj
    return {k.lower(): v for k,v in obj.items()}

# Extract before products
before_products = []
try:
    bl = before.get('product-list', {}).get('content', {}).get('value', [])
    if isinstance(bl, list):
        for r in bl:
            before_products.append(normalize_record(r))
except Exception:
    pass

# Extract after products
after_products = []
ap = after.get('products') or after.get('products')
if isinstance(ap, list):
    for r in ap:
        # r may have uppercase keys
        nr = {}
        for k,v in r.items():
            nr[k.lower()] = v
        after_products.append(nr)

# index
def safe_int(v):
    try:
        return int(v)
    except Exception:
        return None

before_by_id = { safe_int(r.get('id')): r for r in before_products }
after_by_id = { safe_int(r.get('id')): r for r in after_products }
# fallback by sku/name
before_by_sku = { r.get('sku'): r for r in before_products if r.get('sku') }
after_by_sku = { r.get('sku'): r for r in after_products if r.get('sku') }

# compare products by id or sku
keys = set()
keys.update(k for k in before_by_id.keys() if k is not None)
keys.update(k for k in after_by_id.keys() if k is not None)

for key in keys:
    b = before_by_id.get(key)
    a = after_by_id.get(key)
    entry = {'id': key, 'diffs': {}}
    if b and a:
        for field in ['name','description','sku','category','unit']:
            bv = b.get(field)
            av = a.get(field)
            if bv != av:
                entry['diffs'][field] = {'before': bv, 'after': av}
    elif b and not a:
        entry['diffs']['missing_after'] = b
    elif a and not b:
        entry['diffs']['added_after'] = a
    if entry['diffs']:
        report['products'].append(entry)

# compare by sku for those without id
for sku in set(list(before_by_sku.keys())+list(after_by_sku.keys())):
    if sku in before_by_sku and sku in after_by_sku:
        b = before_by_sku[sku]; a = after_by_sku[sku]
        if b.get('id') in before_by_id or a.get('id') in after_by_id:
            continue
        entry = {'sku': sku, 'diffs': {}}
        for field in ['name','description','category','unit']:
            bv = b.get(field); av = a.get(field)
            if bv != av:
                entry['diffs'][field] = {'before': bv, 'after': av}
        if entry['diffs']:
            report['products'].append(entry)

# orders
before_orders = before.get('order-list', {}).get('content', {}).get('value', []) or []
after_orders = after.get('orders', [])
for i,b in enumerate(before_orders):
    bnorm = normalize_record(b)
    aid = bnorm.get('id')
    match = None
    for a in after_orders:
        if str(a.get('id')) == str(aid): match = a; break
    if match:
        diffs = {}
        for f in ['status']:
            bv = bnorm.get(f); av = (match.get(f.lower()) if isinstance(match, dict) else None)
            if bv != av:
                diffs[f] = {'before': bv, 'after': av}
        if diffs:
            report['orders'].append({'id': aid, 'diffs': diffs})

# inventory from before: inventory-detail key
before_inv = []
try:
    bv = before.get('inventory-detail', {}).get('content', {}).get('value', [])
    if isinstance(bv, list):
        for r in bv: before_inv.append(normalize_record(r))
except Exception:
    pass
after_inv = after.get('inventory_records', []) or after.get('inventory_records') or []
# normalize after_inv keys lower
after_inv_norm = []
for r in after_inv:
    after_inv_norm.append({k.lower(): v for k,v in r.items()})

# compare inventory by id
for b in before_inv:
    bid = b.get('id')
    match = None
    for a in after_inv_norm:
        if str(a.get('id')) == str(bid): match = a; break
    if match:
        diffs = {}
        for f in ['type','note']:
            bv = b.get(f); av = match.get(f)
            if bv != av:
                diffs[f] = {'before': bv, 'after': av}
        if diffs:
            report['inventory_records'].append({'id': bid, 'diffs': diffs})

# ai_chats
before_ai = before.get('ai-chat', {}).get('content', {}) or {}
after_ai = after.get('ai_chats', []) or after.get('ai_chats') or []
# before_ai may be single object
if isinstance(before_ai, dict) and before_ai.get('reply'):
    # find matching reply in after
    brep = before_ai.get('reply')
    for a in after_ai:
        if 'reply' in a:
            ar = a.get('reply')
            if brep != ar:
                report['ai_chats'].append({'before': brep, 'after': ar})

out_path = 'encoding_diff_report.json'
with open(out_path, 'w', encoding='utf-8') as f:
    json.dump(report, f, ensure_ascii=False, indent=2)
print('Wrote', out_path)
