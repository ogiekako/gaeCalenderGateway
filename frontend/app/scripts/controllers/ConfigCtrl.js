'use strict';

angular.module('frontendApp').controller('ConfigCtrl',
  function ($scope, BaseService, Utils, GlobalError, User) {

    /* vars */

    $scope.conf = {};
    $scope.data = {
      calendarList: [],
      calendarListLoading: false,
      waitingEmail: false
    };

    /* init */

    BaseService.getConfig().then(
      function ok(config) {
        $scope.conf = config;
        $scope.currentUserId = User.userId;
      },
      Utils.handleError
    );

    /* functions in $scope */

    $scope.updateContactEmail = function () {
      $scope.data.waitingEmail = true;
      BaseService.setContactEmail($scope.conf.contactEmail).then(
          null,
          Utils.handleError
        ).finally(function () {
          $scope.data.waitingEmail = false;
        });
    };

    $scope.disableContactEmail = function () {
      $scope.conf.contactEmail = '';
      $scope.updateContactEmail();
    };

    $scope.listCalendars = function () {
      $scope.data.calendarListLoading = true;
      BaseService.listCalendars().then(
        function ok(calList) {
          $scope.data.calendarList = calList;
        },
        Utils.handleError
      ).finally(function () {
          $scope.data.calendarListLoading = false;
        });
    };

    $scope.useCalendar = function (calId) {
      BaseService.setCalendar(calId).then(
        function ok(status) {
          console.log('cal id updated to ', calId);
          $scope.conf.calendarId = calId;
        },
        Utils.handleError
      );

    };
  }
);
