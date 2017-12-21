pipeline {
  agent any
  stages {
    stage('Build') {
      parallel {
        stage('Build') {
          steps {
            sh 'echo "hello"'
            echo 'first'
          }
        }
        stage('test') {
          steps {
            echo 'test'
          }
        }
      }
    }
    stage('confirm') {
      parallel {
        stage('confirm') {
          steps {
            echo 'confirm'
          }
        }
        stage('confirm2') {
          steps {
            echo 'confirm2'
          }
        }
      }
    }
    stage('anlisty') {
      steps {
        echo 'anlisty'
      }
    }
  }
}