'use strict';

angular.module('frontendApp').service('GlobalError', [
  '$rootScope',
  function ($rootScope) {
    $rootScope.globalErrorMsg = [];

    this.show = function (msg, excp) {
      $rootScope.globalErrorMsg.push(msg);
      if (excp) {
        console.error(excp);
      }
    };

    this.clear = function () {
      $rootScope.globalErrorMsg = [];
    };
  }
]);