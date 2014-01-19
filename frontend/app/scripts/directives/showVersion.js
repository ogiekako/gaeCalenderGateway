'use strict';
angular.module('frontendApp').directive('showVersion',
  function (Version) {
    return {
      scope: {
      },
      transclude: true,
      template: '<span class="version-tag">{{v.id}}</span>',
      restrict: 'EA',
      link: function (scope, element) {
        scope.v = Version;
      }
    };
  });