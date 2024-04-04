docker-compose build
docker-compose push

kubectl apply -f ./deployments/analytics-dep.yaml
kubectl apply -f ./deployments/auth-dep.yaml
kubectl apply -f ./deployments/events-dep.yaml
kubectl apply -f ./deployments/feed-dep.yaml
kubectl apply -f ./deployments/groups-dep.yaml
kubectl apply -f ./deployments/ingress.yaml