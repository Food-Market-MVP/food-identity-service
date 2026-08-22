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
        POSTGRES_CONTAINER = "food-identity-postgres-${BUILD_NUMBER}"
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
                        set -a
                        . "$CI_ENV_FILE"
                        set +a

                        docker rm -f "$POSTGRES_CONTAINER" >/dev/null 2>&1 || true
                        docker run --detach --rm --name "$POSTGRES_CONTAINER" \\
                          --publish 127.0.0.1::5432 \\
                          --env POSTGRES_DB="$DB_NAME" \\
                          --env POSTGRES_USER="$DB_USERNAME" \\
                          --env POSTGRES_PASSWORD="$DB_PASSWORD" \\
                          postgres:15

                        until docker exec "$POSTGRES_CONTAINER" pg_isready -U "$DB_USERNAME" -d "$DB_NAME"; do
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
