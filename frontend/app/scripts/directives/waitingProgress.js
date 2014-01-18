'use strict';
angular.module('frontendApp')
  .directive('waitingProgress', function () {
    return {
      scope: {
        msg: '@',
        loading: '@show'
      },
      transclude: true,
      template: '<div class="center-block waiting-progress" style="width: 50%" ng-if="loading">\n    <span class="waiting-progress__msg">{{msg}}</span>\n    <div class="progress progress-striped active">\n        <div class="progress-bar" role="progressbar" style="width: 45%"></div>\n    </div>\n</div>',
      restrict: 'EA'
    };
  });