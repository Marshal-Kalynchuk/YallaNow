
# Setup Instructions

## Authenticating gcloud
Before starting, make sure to authenticate gcloud:
```bash
gcloud auth application-default login
```

## Configuring Docker Repository
Configure your Docker repository to use the Google Artifact Registry:
```bash
gcloud auth configure-docker us-central1-docker.pkg.dev
```

The images in the repository are located at:
`us-central1-docker.pkg.dev/yallanow-413400/yallanow-services`

## Building and Pushing Images
To build the images, run the following command in the services directory:
```bash
docker-compose build
```

Then, push the built images to the Google Cloud Artifact Registry:
```bash
docker-compose push
```

## Deploying Kubernetes Secrets
- Note: The credentials may already be added.

Navigate to the secrets folder and run the following commands to add credential files to Google Cloud Kubernetes:
Adding Firebase credentials:
```bash
kubectl create secret generic firebase-credentials --from-file=firebase-credentials.json
```

Adding Google credentials:
```bash
kubectl create secret generic google-credentials --from-file=google-credentials.json
```

Applying secrets from the `secrets.yaml` file:
```bash
kubectl apply -f secrets.yaml
```

## Deploying Services and Ingress
Navigate to the deployments folder and run the following command to deploy all services and ingress:
```bash
kubectl apply -f analytics-dep.yaml -f auth-dep.yaml -f events-dep.yaml -f feed-dep.yaml -f groups-dep.yaml -f ingress.yaml
```

