'use strict';

angular.module('frontendApp').controller('IcalInfoDetailCtrl',
  function ($scope, $log, $routeParams, $location, GlobalError, IcalInfoService) {

    function handleError(msg) {
      $scope.action = false;
      GlobalError.show(msg);
    }

    // a item copy to detect changes
    var copy;

    $scope.statusOptions = [ 'PARSED', 'CAL_ERROR', 'CAL_ADDED', 'CAL_UPDATED', 'CAL_REMOVED' ];
    $scope.action = false;

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


    IcalInfoService.getItem($routeParams.id).then(
      function ok(item) {
        $scope.item = item;
        copy = angular.copy(item);
      },
      handleError
    );


  });
