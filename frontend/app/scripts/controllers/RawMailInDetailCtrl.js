'use strict';

angular.module('frontendApp').controller('RawMailInDetailCtrl',
  function ($scope, $routeParams, $location, GlobalError, RawMailInService, SiteDataCache) {

    function handleError(msg) {
      $scope.saving = false;
      GlobalError.show(msg);
    }

    function loadItem(id) {
      RawMailInService.getItem(id).then(
        function ok(mailItem) {
          $scope.mailItem = mailItem;
        },
        handleError
      );
    }

    // +1 or -1
    function changeItemIndex(delta) {
      var index = _.findIndex(SiteDataCache.rawMailList, { id: $scope.mailItem.id });
      var item = SiteDataCache.rawMailList[index + delta];
      if (!item) {
        return;
      }
      loadItem(item.id);
    }

    $scope.statusOptions = [ 'INCOMING', 'PROCESSED', 'ERROR' ];
    $scope.saving = false;
    $scope.nextPrevAvailable = !!SiteDataCache.rawMailList;

    $scope.save = function () {
      $scope.saving = true;
      RawMailInService.updateItem($scope.mailItem).then(
        function ok() {
          $location.path('/rawMailIn');
        },
        handleError
      );
    };

    $scope.next = function () {
      changeItemIndex(1);
    };

    $scope.previous = function () {
      changeItemIndex(-1);
    };

    loadItem($routeParams.mailId);

  });
