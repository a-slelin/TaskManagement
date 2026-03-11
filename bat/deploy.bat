@echo off
title Build and Deploy Task Management System

:: Переход в родительскую папку
cd ..

:: 1. Запуск тестов
echo Running Maven tests...
call mvn test
if %errorlevel% neq 0 (
    echo Tests failed.
    exit /b 1
)
echo Tests passed.

:: 2. Сборка проекта без тестов
echo Building project (skip tests)...
call mvn clean install -DskipTests
if %errorlevel% neq 0 (
    echo Build failed.
    exit /b 1
)
echo Build successful.

:: 3. Сборка Docker-образа
echo Building Docker image aslelin/task-management-system:1.0.0...
call docker build -t aslelin/task-management-system:1.0.0 -t aslelin/task-management-system:latest .
if %errorlevel% neq 0 (
    echo Docker build failed.
    exit /b 1
)
echo Docker image built.

:: 4. Отправка образа в реестр
echo Pushing Docker image to registry...
call docker push aslelin/task-management-system:1.0.0
if %errorlevel% neq 0 (
    echo Docker push failed.
    exit /b 1
)
echo Docker image pushed.

exit /b 0