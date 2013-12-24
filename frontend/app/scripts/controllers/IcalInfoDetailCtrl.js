'use strict';

angular.module('frontendApp').controller('IcalInfoDetailCtrl',
  function ($scope, $routeParams, $location, GlobalError, IcalInfoService) {

    function handleError(msg) {
      $scope.saving = false;
      GlobalError.show(msg);
    }

    $scope.statusOptions = [ 'PARSED', 'ADD_SUCCESS', 'ADD_ERROR' ];
    $scope.saving = false;

    $scope.save = function () {
      $scope.saving = true;
      IcalInfoService.updateItem($scope.item).then(
        function ok() {
          $location.path('/ical');
        },
        handleError
      );
    };


    IcalInfoService.getItem($routeParams.id).then(
      function ok(item) {
        $scope.item = item;
      },
      handleError
    );
    
    

  });
