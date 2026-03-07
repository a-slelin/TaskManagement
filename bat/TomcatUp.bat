@echo off
title Tomcat Cluster Launcher
echo ===== ЗАПУСК Tomcat =====
echo.

cd /d D:\Tomcat 11.0\bin

echo Останавливаем если запущен...
start "Tomcat down" cmd /c "shutdown.bat"
timeout /t 3 /nobreak >nul

echo Запускаем...
start "Tomcat up" cmd /c "startup.bat"
timeout /t 3 /nobreak >nul

echo.
echo ===== Tomcat запущен! =====
echo.

pause