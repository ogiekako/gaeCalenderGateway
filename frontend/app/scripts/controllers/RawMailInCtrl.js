'use strict';

angular.module('frontendApp').controller('RawMailInCtrl',
  function ($scope, $location, GlobalError, RawMailInService, Utils) {

    function handleError(msg) {
      GlobalError.show(msg);
      $scope.loading = false;
    }

    $scope.edit = function (id) {
      $location.path('/rawMailIn/' + id);
    };

    $scope.loading = true;
    $scope.rawMails = [];
    RawMailInService.getList().then(
      function ok(mailList) {
        $scope.rawMails = mailList;
        $scope.loading = false;
      },
      handleError
    );

  });
