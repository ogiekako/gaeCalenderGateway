'use strict';

angular.module('frontendApp')
  .controller('NavCtrl', [
    '$scope', '$route', '$location',
    function ($scope, $route, $location) {

      this.isActive = {};
      var self = this;
      $scope.$on('$routeChangeSuccess', function() {
        self.isActive = {};
        self.isActive[$location.path()] = true;
      });
    }
  ]);
