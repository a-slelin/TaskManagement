# Запуск в кластере K8S

Если вы хотите запустить приложение в кластере k8s или в своем локальном minikube, то используйте два файла-манифеста
для этого.

```shell
# Выделяем "диск" для хранения базы данных; 
minikube ssh;
> sudo mkdir -p /mnt/data/postgres;
> sudo chmod 777 /mnt/data/postgres;
> exit;

# Применяем манифесты;
kubectl apply -f postgres.yaml;
kubectl apply -f app.yaml;

# Создаем тунель чтоб получить доступ извне;
kubectl port-forward service/app 8080:8080;
```