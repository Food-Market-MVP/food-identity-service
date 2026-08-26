pipeline {
    agent { label 'docker' }

    tools {
        jdk 'jdk21'
    }

    options {
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 20, unit: 'MINUTES')
    }

    environment {
        POSTGRES_CONTAINER = "food-identity-postgres-${BUILD_TAG}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test database and verify') {
            steps {
                withCredentials([file(credentialsId: 'food-identity-ci-env', variable: 'CI_ENV_FILE')]) {
                    sh '''#!/bin/sh
                        while IFS= read -r credential || [ -n "$credential" ]; do
                          case "$credential" in
                            ''|\#*) continue ;;
                            DB_HOST=*|DB_PORT=*|DB_NAME=*|DB_USERNAME=*|DB_PASSWORD=*)
                              export "$credential"
                              ;;
                            APP_SECURITY_ADMIN=*|APP_SECURITY_USER=*|APP_SECURITY_PASSWORD=*|APP_SECURITY_SECRET=*)
                              export "$credential"
                              ;;
                            *)
                              echo "Invalid entry in CI credential file" >&2
                              exit 1
                              ;;
                          esac
                        done < "$CI_ENV_FILE"

                        docker rm -f "$POSTGRES_CONTAINER" >/dev/null 2>&1 || true
                        docker run --detach --name "$POSTGRES_CONTAINER" \\
                          --publish 127.0.0.1::5432 \\
                          --env POSTGRES_DB="$DB_NAME" \\
                          --env POSTGRES_USER="$DB_USERNAME" \\
                          --env POSTGRES_PASSWORD="$DB_PASSWORD" \\
                          postgres:15.19

                        attempts=0
                        until docker exec "$POSTGRES_CONTAINER" pg_isready -U "$DB_USERNAME" -d "$DB_NAME"; do
                          attempts=$((attempts + 1))
                          if [ "$attempts" -ge 30 ]; then
                            echo "Postgres failed to become ready after 30 attempts" >&2
                            docker logs "$POSTGRES_CONTAINER" >&2 || true
                            exit 1
                          fi
                          sleep 1
                        done

                        DB_PORT="$(docker port "$POSTGRES_CONTAINER" 5432/tcp | sed 's/.*://')"
                        export DB_PORT
                        chmod +x mvnw
                        ./mvnw --batch-mode --no-transfer-progress clean verify
                    '''
                }
            }
        }

        stage('Build container image') {
            steps {
                sh 'docker build --tag "food-identity:${BUILD_NUMBER}" .'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
            archiveArtifacts allowEmptyArchive: true, artifacts: 'target/food-identity-*.jar,target/site/jacoco/**'
            sh 'docker rm -f "$POSTGRES_CONTAINER" >/dev/null 2>&1 || true'
            deleteDir()
        }
    }
}
