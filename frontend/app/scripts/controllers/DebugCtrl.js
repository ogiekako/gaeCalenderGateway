'use strict';

angular.module('frontendApp').controller('DebugCtrl',
  function ($scope, ConfigService, DebugService, Utils, AppInfo) {
    var DAY_IN_MS = 1000 * 60 * 60 * 24;

    function addPreviews(eventData) {
      eventData.forEach(function (item) {
        item.descriptionPreview = Utils.shorter(item.description, 60);
      });
    }

    /* init */

    $scope.appId = AppInfo.id;
    $scope.calendarId = '...';
    $scope.eventList = [];
    $scope.eventListLoading = false;

    $scope.begin = {
      time: new Date(),
      open: false
    };
    $scope.end = {
      time: new Date($scope.begin.time.getTime() + DAY_IN_MS * 7),
      open: false
    };

    ConfigService.getConfig().then(
      function ok(config) {
        $scope.calendarId = config.calendarId;
      },
      Utils.handleError
    );

    /* scope functions */

    $scope.openCalendar = function (picker, $event) {
      $event.preventDefault();
      $event.stopPropagation();

      picker.open = true;
    };

    $scope.listEvents = function () {
      console.log('show events for ', $scope.begin.time, $scope.end.time);
      $scope.eventListLoading = true;

      DebugService.getEvents($scope.begin.time, $scope.end.time).then(
        function ok(events) {
          addPreviews(events);
          $scope.eventList = events;
        },
        Utils.handleError
      ).finally(function () {
          $scope.eventListLoading = false;
        });

    };
  });
