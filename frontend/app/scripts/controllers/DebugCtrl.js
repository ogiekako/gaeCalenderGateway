'use strict';

angular.module('frontendApp').controller('DebugCtrl',
  function ($scope, BaseService, DebugService, Utils) {
    var DAY_IN_MS = 1000 * 60 * 60 * 24;

    function addPreviews(eventData) {
      eventData.forEach(function (item) {
        item.descriptionPreview = Utils.shorter(item.description, 60);
      });
    }

    $scope.calendarId = '...';
    $scope.eventList = [];
    $scope.form = {
      days: 30
    };

    BaseService.getConfig().then(
      function ok(config) {
        $scope.calendarId = config.calendarId;
      },
      Utils.handleError
    );

    $scope.listEvents = function () {
      if (!$scope.form.days) {
        return;
      }
      console.log('show events for ', $scope.form.days);

      var now = new Date();
      var nowTrimmed = new Date(now.getFullYear(), now.getMonth(), now.getDate());
      var sinceTs = nowTrimmed.getTime() - ($scope.form.days * DAY_IN_MS);

      DebugService.getEvents(sinceTs).then(
        function ok(events) {
          addPreviews(events);
          $scope.eventList = events;
        },
        Utils.handleError
      );

    };
  });
