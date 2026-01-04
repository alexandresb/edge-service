# construction de l'image
custom_build(
    # nom de l'image
    ref = 'edge-service',
    #commande pour créer l'image OCI avec Buildpacks
    command = './gradlew bootBuildImage --imageName $EXPECTED_REF',
    #fichiers / dossiers observés par Tilt pour déclencher un nouveau build en cas de mise à jour
    deps = ['build.gradle', 'src']
)

# déploiement dans K8s
k8s_yaml(kustomize('k8s'))

# port-forwarding
k8s_resource('edge-service', port_forwards=['9000'])