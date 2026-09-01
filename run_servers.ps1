$env:PATH = "C:\Users\Ayush.Keshri\.cache\codex-runtimes\codex-primary-runtime\dependencies\node\bin;" + $env:PATH
$pnpm = "C:\Users\Ayush.Keshri\.cache\codex-runtimes\codex-primary-runtime\dependencies\node\node_modules\pnpm\bin\pnpm.cjs"

Write-Host "Starting backend server on port 5000..." -ForegroundColor Green
Start-Process -NoNewWindow -FilePath "node" -ArgumentList "backend/server.js"

Write-Host "Starting frontend server on port 3000..." -ForegroundColor Green
node $pnpm --dir frontend dev
