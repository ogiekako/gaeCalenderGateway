'use strict';

angular.module('frontendApp').controller('ConfigCtrl', [
  '$scope', 'BaseService', 'GlobalError', 'User',
  function ($scope, BaseService, GlobalError, User) {
    function handleError(msg) {
      GlobalError.show(msg);
    }

    BaseService.getConfig().then(
      function ok(config) {
        $scope.conf = config;
        $scope.currentUserId = User.userId;
      },
      handleError
    );

    $scope.calendarList = [];
    $scope.calendarListLoading = false;

    $scope.listCalendars = function () {
      $scope.calendarListLoading = true;
      BaseService.listCalendars().then(
        function ok(calList) {
          $scope.calendarList = calList;
        },
        handleError
      ).finally(function () {
          $scope.calendarListLoading = false;
        });
    };

    $scope.useCalendar = function (calId) {
      BaseService.setCalendar(calId).then(
        function ok(status) {
          console.log('cal id updated to ', calId);
          $scope.conf.calendarId = calId;
        },
        handleError
      );


    };
  }
]);
