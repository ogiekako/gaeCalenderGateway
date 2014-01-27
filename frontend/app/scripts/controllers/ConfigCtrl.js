'use strict';

angular.module('frontendApp').controller('ConfigCtrl',
  function ($scope, BaseService, ConfigService, Utils, GlobalError, User) {

    /* vars */

    $scope.conf = {};
    $scope.ui = {
      calendarList: [],
      calendarListLoading: false,
      waitingEmail: false,
      contactEmail: '',
      mailTestResult: ''
    };

    /* init */

    ConfigService.getConfig().then(
      function ok(config) {
        $scope.conf = config;
        $scope.currentUserId = User.userId;
        $scope.ui.contactEmail = angular.copy($scope.conf.contactEmail);
      },
      Utils.handleError
    );


    /* functions in $scope */

    $scope.sendTestMail = function () {
      $scope.ui.waitingEmail = true;
      ConfigService.testMailConfiguration().then(
        function ok() {
          $scope.ui.mailTestResult = 'success';
          $scope.ui.waitingEmail = false;
        },
        function error() {
          $scope.ui.mailTestResult = 'error!';
          $scope.ui.waitingEmail = false;
        });
    };

    $scope.updateContactEmail = function () {
      $scope.ui.waitingEmail = true;
      ConfigService.setContactEmail($scope.ui.contactEmail).then(
        function ok() {
          $scope.conf.contactEmail = $scope.ui.contactEmail;
        },
        Utils.handleError
      ).finally(function () {
          $scope.ui.waitingEmail = false;
        });
    };

    $scope.disableContactEmail = function () {
      $scope.ui.contactEmail = '';
      $scope.updateContactEmail();
    };

    $scope.listCalendars = function () {
      $scope.ui.calendarListLoading = true;
      BaseService.listCalendars().then(
        function ok(calList) {
          $scope.ui.calendarList = calList;
        },
        Utils.handleError
      ).finally(function () {
          $scope.ui.calendarListLoading = false;
        });
    };

    $scope.useCalendar = function (calId) {
      ConfigService.setCalendar(calId).then(
        function ok(status) {
          console.log('cal id updated to ', calId);
          $scope.conf.calendarId = calId;
        },
        Utils.handleError
      );

    };
  }
);
