'use strict';

angular.module('frontendApp').controller('IcalInfoCtrl',
  function ($scope, $location, GlobalError, IcalInfoService, Utils) {

    function handleError(msg) {
      GlobalError.show(msg);
    }

    $scope.edit = function (id) {
      $location.path('/ical/' + id);
    };

    $scope.list = [];
    IcalInfoService.getList().then(
      function ok(list) {
        $scope.list = list;
      },
      handleError
    );

  });
