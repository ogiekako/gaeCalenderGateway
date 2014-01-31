'use strict';

angular.module('frontendApp').controller('RawMailInCtrl',
  function ($scope, $location, GlobalError, RawMailInService, Utils) {

    function handleError(msg) {
      GlobalError.show(msg);
      $scope.loading = false;
    }

    /* init */
    $scope.loading = true;
    $scope.rawMails = [];
    RawMailInService.getList().then(
      function ok(mailList) {
        $scope.rawMails = mailList;
        $scope.loading = false;
      },
      handleError
    );

    /* scope functions */
    $scope.edit = function (id) {
      $location.path('/rawMailIn/' + id);
    };

    $scope.delete = function (index, event) {
      event.stopPropagation();
      RawMailInService.deleteItem($scope.rawMails[index].id).then(
        function ok() {
          $scope.rawMails.splice(index, 1);
        },
        handleError
      );
    };
  });
