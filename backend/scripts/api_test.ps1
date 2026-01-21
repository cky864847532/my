$base='http://localhost:8080'
$pwd = 'testpass'
$authValue = "Basic dXNlcjp0ZXN0cGFzcw==" # base64 of user:testpass
$headers = @{ Authorization = $authValue }

$results = @{}

function DoRequest($name, $method, $url, $body=$null) {
    Write-Output "-> $method $url"
    try {
        if ($body -ne $null) {
            $json = $body | ConvertTo-Json -Depth 10
            $resp = Invoke-WebRequest -Uri ($base + $url) -Method $method -Body $json -ContentType 'application/json' -Headers $headers -UseBasicParsing -ErrorAction Stop
        } else {
            $resp = Invoke-WebRequest -Uri ($base + $url) -Method $method -ContentType 'application/json' -Headers $headers -UseBasicParsing -ErrorAction Stop
        }
        $content = $resp.Content
        $status = $resp.StatusCode.Value__
        $obj = @{status=$status; content=$null}
        try { $obj.content = ($content | ConvertFrom-Json) } catch { $obj.content = $content }
        $results[$name] = $obj
        return $obj
    } catch {
        $errResp = $_.Exception.Response
        if ($errResp -ne $null) {
            $sr = New-Object System.IO.StreamReader($errResp.GetResponseStream())
            $text = $sr.ReadToEnd(); $sr.Close()
            $results[$name] = @{status = $errResp.StatusCode.Value__; content = $text}
        } else {
            $results[$name] = @{status = 'ERROR'; content = $_.Exception.Message}
        }
        return $results[$name]
    }
}

# 1. stats
DoRequest 'stats-sales' 'GET' '/api/stats/sales'

# 2. create product
$prod = @{name='AutoProduct'; sku='AUT-001'; category='test'; unit='pcs'; price = ([double]9.99); hazard = 0; description='Created by automated test'}
$resp = DoRequest 'product-create' 'POST' '/api/products' $prod
$productId = $null
if ($resp.content -is [System.Collections.Hashtable] -and $resp.content.id) { $productId = [int]$resp.content.id } elseif ($resp.content.id) { $productId = [int]$resp.content.id }

# 3. list products
DoRequest 'product-list' 'GET' '/api/products'

# 4. get product by id (if available)
if ($productId) { DoRequest 'product-get' 'GET' ("/api/products/$productId") }

# 5. adjust price
if ($productId) { DoRequest 'product-price' 'POST' ("/api/products/$productId/price") @{price = ([double]12.5)} }

# 6. inventory in
if ($productId) { $prodId = [int]$productId; $invIn = @{productId=$prodId; qty=[int]100; batch='BATCH1'; expireAt='2026-12-31'; operatorId=[int]1; note='test stock in'}; DoRequest 'inventory-in' 'POST' '/api/inventory/in' $invIn }

# 7. inventory detail
if ($productId) { DoRequest 'inventory-detail' 'GET' ("/api/inventory/$productId") }

$cid = [int]1
$pidForOrder = 0
if ($productId) { $pidForOrder = [int]$productId }
$order = @{customerId=$cid; items = @(@{productId=$pidForOrder; qty=[int]2; price=[double]12.5})}
$respOrder = DoRequest 'order-create' 'POST' '/api/orders' $order
$orderId = $null
if ($respOrder.content -is [System.Collections.Hashtable] -and $respOrder.content.orderId) { $orderId = $respOrder.content.orderId } elseif ($respOrder.content.orderId) { $orderId = $respOrder.content.orderId }

# 9. list orders
DoRequest 'order-list' 'GET' '/api/orders'

# 10. get order
if ($orderId) { DoRequest 'order-get' 'GET' ("/api/orders/$orderId") }

# 11. inventory out
if ($productId) { $prodId = [int]$productId; $invOut = @{productId=$prodId; qty=[int]5; operatorId=[int]1; note='test stock out'}; DoRequest 'inventory-out' 'POST' '/api/inventory/out' $invOut }

# 12. inventory adjust
if ($productId) { $prodId = [int]$productId; $adj = @{productId=$prodId; qty=[int]95; note='test stock adjust'}; DoRequest 'inventory-adjust' 'POST' '/api/inventory/adjust' $adj }

## 13. AI chat
$ai = @{customerId=[int]1; message='Please provide test suggestions.'}
DoRequest 'ai-chat' 'POST' '/api/ai/chat' $ai

# Save results
$outFile = '.\\api_test_results.json'
$json = $results | ConvertTo-Json -Depth 10
# Write UTF-8 without BOM using .NET API to avoid BOM-related issues
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText((Resolve-Path $outFile).Path, $json, $utf8NoBom)
Write-Output "Saved results to $(Resolve-Path $outFile) (UTF-8 no BOM)"
