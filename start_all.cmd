@echo off
REM 一键启动前后端服务
start cmd /k "cd /d backend && mvn spring-boot:run"
REM 等待后端启动
ping 127.0.0.1 -n 2 >nul
start cmd /k "cd /d frontend && npm run dev -- --host 0.0.0.0 --port 5173"
