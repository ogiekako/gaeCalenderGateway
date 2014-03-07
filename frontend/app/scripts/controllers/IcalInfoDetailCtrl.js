'use strict';

angular.module('frontendApp').controller('IcalInfoDetailCtrl',
  function ($scope, $log, $routeParams, $location, GlobalError, IcalInfoService, SiteDataCache) {

    function handleError(msg) {
      $scope.action = false;
      GlobalError.show(msg);
    }

    function loadItem(id) {
      IcalInfoService.getItem(id).then(
        function ok(item) {
          $scope.item = item;
          copy = angular.copy(item);
        },
        handleError
      );
    }

    // +1 or -1
    function changeItemIndex(delta) {
      var index = _.findIndex(SiteDataCache.iCalList, { id: $scope.item.id });
      var item = SiteDataCache.iCalList[index + delta];
      if (!item) {
        return;
      }
      loadItem(item.id);
    }

    // a item copy to detect changes
    var copy;

    $scope.statusOptions = [ 'PARSED', 'CAL_ERROR', 'CAL_ADDED', 'CAL_UPDATED', 'CAL_REMOVED' ];
    $scope.action = false;
    $scope.nextPrevAvailable = !!SiteDataCache.iCalList;

    $scope.save = function () {
      $scope.action = true;
      IcalInfoService.updateItem($scope.item).then(
        function ok() {
          $location.path('/ical');
        },
        handleError
      );
    };

    $scope.addToCalendar = function () {
      $scope.action = true;
      function addToCal() {
        $log.log('adding to calendar');
        IcalInfoService.addToCalendar([$scope.item.id]).then(
          function ok() {
            $location.path('/ical');
          },
          handleError
        );
      }

      if (angular.equals($scope.item, copy)) {
        addToCal();
      } else {
        $log.log('Entry was changed => saving it before adding to calendar.');
        IcalInfoService.updateItem($scope.item).then(addToCal(), handleError);
      }
    };

    $scope.next = function () {
      changeItemIndex(1);
    };

    $scope.previous = function () {
      changeItemIndex(-1);
    };


    loadItem($routeParams.id);

  });
