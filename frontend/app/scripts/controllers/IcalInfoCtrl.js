'use strict';

angular.module('frontendApp').controller('IcalInfoCtrl',
  function ($scope, $location, GlobalError, IcalInfoService, Utils) {

    function handleError(msg) {
      GlobalError.show(msg);
      $scope.loading = false;
    }

    /* init */
    $scope.loading = true;
    $scope.list = [];

    IcalInfoService.getList().then(
      function ok(list) {
        $scope.list = list;
        $scope.loading = false;
      },
      handleError
    );

    /* scope functions */
    $scope.edit = function (id) {
      $location.path('/ical/' + id);
    };

    $scope.delete = function (index, event) {
      event.stopPropagation();
      IcalInfoService.deleteItem($scope.list[index].id).then(
        function ok() {
          $scope.list.splice(index, 1);
        },
        handleError
      );
    };

  });
