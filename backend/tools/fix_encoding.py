#!/usr/bin/env python3
import json, re, sys, os

def has_non_ascii(s):
    return any(ord(c) > 127 for c in s)

def has_cjk(s):
    return re.search(r'[\u4e00-\u9fff]', s) is not None

def try_fix(s):
    try:
        b = s.encode('latin-1')
        cand = b.decode('utf-8')
        if has_cjk(cand):
            return cand
    except Exception:
        pass
    return s

def walk(obj, stats):
    if isinstance(obj, dict):
        return {k: walk(v, stats) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [walk(v, stats) for v in obj]
    elif isinstance(obj, str):
        if has_non_ascii(obj):
            fixed = try_fix(obj)
            if fixed != obj:
                stats['replacements'] += 1
                return fixed
        return obj
    else:
        return obj

def main():
    inpath = 'api_test_results.json'
    outpath = 'api_test_results_clean.json'
    if len(sys.argv) > 1:
        inpath = sys.argv[1]
    if len(sys.argv) > 2:
        outpath = sys.argv[2]
    if not os.path.exists(inpath):
        print(f'Input file not found: {inpath}', file=sys.stderr)
        sys.exit(2)
    # Use utf-8-sig to tolerate BOM at file start
    with open(inpath, 'r', encoding='utf-8-sig') as f:
        data = json.load(f)
    stats = {'replacements': 0}
    fixed = walk(data, stats)
    with open(outpath, 'w', encoding='utf-8') as f:
        json.dump(fixed, f, ensure_ascii=False, indent=4)
    print(f'Wrote {outpath}. Replacements: {stats["replacements"]}')

if __name__ == '__main__':
    main()
