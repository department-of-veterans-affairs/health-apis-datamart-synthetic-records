def saunter(scriptName) {
  withCredentials([
    usernamePassword(
      credentialsId: 'HEALTH_APIS_RELEASES_NEXUS_USERNAME_PASSWORD',
      usernameVariable: 'NEXUS_USERNAME',
      passwordVariable: 'NEXUS_PASSWORD'),
    usernamePassword(
      credentialsId: 'LABMASTER_USERNAME_PASSWORD',
      usernameVariable: 'LABMASTER_USERNAME',
      passwordVariable: 'LABMASTER_PASSWORD'),
    usernamePassword(
      credentialsId: 'LABUSER_USERNAME_PASSWORD',
      usernameVariable: 'LABUSER_USERNAME',
      passwordVariable: 'LABUSER_PASSWORD'),
    usernamePassword(
      credentialsId: 'STGLABMASTER_USERNAME_PASSWORD',
      usernameVariable: 'STGLABMASTER_USERNAME',
      passwordVariable: 'STGLABMASTER_PASSWORD'),
    usernamePassword(
      credentialsId: 'STGLABUSER_USERNAME_PASSWORD',
      usernameVariable: 'STGLABUSER_USERNAME',
      passwordVariable: 'STGLABUSER_PASSWORD'),
   ]) {
    sh script: scriptName
  }
}

pipeline {
  options {
    buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '30'))
    disableConcurrentBuilds()
    retry(0)
    timeout(time: 120, unit: 'MINUTES')
    timestamps()
  }
  parameters {
    booleanParam(name: 'CLEAN', defaultValue: false, description: "Remove existing database and start from scratch.")
  }
  agent {
    docker {
      registryUrl 'https://index.docker.io/v1/'
      registryCredentialsId 'DOCKER_USERNAME_PASSWORD'
      image 'vasdvp/health-apis-synthetic-records-builder:latest'
      alwaysPull true
    }
  }
  environment {
    ENVIRONMENT = "${["staging_lab", "lab"].contains(env.BRANCH_NAME) ? env.BRANCH_NAME.replaceAll('_','-') : "i-cant-even-w-this"}"
  }
  stages {
    stage('Clean') {
      when {
        beforeInput true
        expression { return env.CLEAN == 'true'}
      }
      input {
        message "Rub-a-dub-dub..."
        ok "Database will be cleaned before inserting records..."
        submitter "bryan.schofield,ian.laflamme,aparcel-va,eclendenning,joshua.hulbert"
      }
      steps {
        saunter('./build.sh clean')
      }
    }
    stage('Build') {
      when {
        expression { return env.ENVIRONMENT != 'i-cant-even-w-this' }
      }
      steps {
        saunter('./build.sh')
      }
    }
  }
    post {
    always {
      script {
         def buildName = sh returnStdout: true, script: '''[ -f .jenkins/build-name ] && cat .jenkins/build-name ; exit 0'''
        currentBuild.displayName = "#${currentBuild.number} - ${buildName}"
        def description = sh returnStdout: true, script: '''[ -f .jenkins/description ] && cat .jenkins/description ; exit 0'''
        currentBuild.description = "${description}"
        if (env.ENVIRONMENT != 'i-cant-even-w-this') {
          sendNotifications()
        }
      }
    }
  }

}
