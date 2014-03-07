'use strict';
angular.module('frontendApp').directive('refresher',
  function (BaseService, $timeout) {
    var TIMEOUT_IN_S = 10;
    return {
      scope: {
      },
      template: '<label title="Every x seconds a keep alive request is send to the server.">\n    <input type="checkbox" ng-model="enabled"> refresher\n</label>\n<span class="glyphicon glyphicon-ok" ng-if="enabled && statusOk"></span>\n<span class="glyphicon glyphicon-remove" ng-if="!statusOk"></span>\n',
      restrict: 'EA',
      link: function (scope, element) {
        function update() {
          if (!scope.enabled) {
            return;
          }

          BaseService.ping().then(
            function ok() {
              scope.statusOk = true;
              $timeout(update, TIMEOUT_IN_S * 1000, false);
            }, function error(data) {
              scope.enabled = false;
              scope.statusOk = false;
              console.log('error!', data);
            }
          );

        }

        scope.enabled = false;
        scope.statusOk = true;
        scope.$watch('enabled', update);
      }
    };
  });