'use strict';

angular.module('frontendApp').controller('RawMailInDetailCtrl',
  function ($scope, $routeParams, $location, GlobalError, RawMailInService) {

    function handleError(msg) {
      $scope.saving = false;
      GlobalError.show(msg);
    }

    $scope.statusOptions = [ 'INCOMING', 'PROCESSED', 'ERROR' ];
    $scope.saving = false;

    $scope.save = function () {
      $scope.saving = true;
      RawMailInService.updateItem($scope.mailItem).then(
        function ok() {
          $location.path('/rawMailIn');
        },
        handleError
      );
    };
    
    
    RawMailInService.getItem($routeParams.mailId).then(
      function ok(mailItem) {
        $scope.mailItem = mailItem;
      },
      handleError
    );
    
    

  });
