'use strict';
angular.module('frontendApp').directive('showVersion',
  function (AppInfo) {
    return {
      scope: {
      },
      transclude: true,
      template: '<span class="version-tag">{{v.version}}</span>',
      restrict: 'EA',
      link: function (scope, element) {
        scope.v = AppInfo;
      }
    };
  });