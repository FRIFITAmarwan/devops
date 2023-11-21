def ENV_NAME = getEnvName(env.BRANCH_NAME)
def CONTAINER_NAME = "devops-" + ENV_NAME
def CONTAINER_TAG = getTag(env.BUILD_NUMBER, env.BRANCH_NAME)
def HTTP_PORT = getHTTPPort(env.BRANCH_NAME)
def EMAIL_RECIPIENTS = "plusmarwan@gmail.com"

pipeline {
    agent any

    environment {
        dockerHome = tool 'dockerlatest2'
        mavenHome = tool 'mavenlatest2'
        PATH = "${dockerHome}/bin:${mavenHome}/bin:${PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with test') {
            steps {
                sh "mvn clean install -DfailIfNoTests=false"
            }
        }

        stage('Sonarqube Analysis') {
            steps {
                withSonarQubeEnv('sonarServer') {
                    sh "mvn sonar:sonar -Dintegration-tests.skip=true -Dmaven.test.failure.ignore=true"
                }
                timeout(time: 1, unit: 'MINUTES') {
                    def qg = waitForQualityGate()
                    if (qg.status != 'OK') {
                        error "Pipeline aborted due to quality gate failure: ${qg.status}"
                    }
                }
            }
        }

        stage("Image Prune") {
            steps {
                script {
                    imagePrune(CONTAINER_NAME)
                }
            }
        }

        stage('Image Build') {
            steps {
                script {
                    imageBuild(CONTAINER_NAME, CONTAINER_TAG)
                }
            }
        }

        stage('Push to Docker Registry') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhubcredentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        pushToImage(CONTAINER_NAME, CONTAINER_TAG, USERNAME, PASSWORD)
                    }
                }
            }
        }

        stage('Run App') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'dockerhubcredentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        runApp(CONTAINER_NAME, CONTAINER_TAG, USERNAME, HTTP_PORT, ENV_NAME)
                    }
                }
            }
        }
    }

    post {
        success {
            script {
                sendEmail(EMAIL_RECIPIENTS)
            }
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}

def imagePrune(containerName) {
    try {
        sh "docker image prune -f"
        sh "docker stop $containerName"
    } catch (ignored) {
    }
}

def imageBuild(containerName, tag) {
    sh "docker build -t $containerName:$tag --pull --no-cache ."
    echo "Image build complete"
}

def pushToImage(containerName, tag, dockerUser, dockerPassword) {
    sh "docker login -u $dockerUser -p $dockerPassword"
    sh "docker tag $containerName:$tag $dockerUser/$containerName:$tag"
    sh "docker push $dockerUser/$containerName:$tag"
    echo "Image push complete"
}

def runApp(containerName, tag, dockerHubUser, httpPort, envName) {
    sh "docker pull $dockerHubUser/$containerName:$tag"
    sh "docker run --rm --env SPRING_ACTIVE_PROFILES=$envName -d -p $httpPort:$httpPort --name $containerName $dockerHubUser/$containerName:$tag"
    echo "Application started on port: ${httpPort} (http)"
}

def sendEmail(recipients) {
    mail(
        to: recipients,
        subject: "Build ${env.BUILD_NUMBER} - ${currentBuild.currentResult} - (${currentBuild.fullDisplayName})",
        body: "Check console output at: ${env.BUILD_URL}/console" + "\n"
    )
}

String getEnvName(String branchName) {
    if (branchName == 'master') {
        return 'prod'
    }
    return (branchName == 'preprod') ? 'uat' : 'dev'
}

String getHTTPPort(String branchName) {
    if (branchName == 'master') {
        return '9999'
    }
    return (branchName == 'preprod') ? '8888' : '7777'
}

String getTag(String buildNumber, String branchName) {
    if (branchName == 'master') {
        return buildNumber + '-unstable'
    }
    return buildNumber + '-stable'
}
