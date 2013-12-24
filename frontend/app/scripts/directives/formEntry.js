'use strict';

angular.module('frontendApp')
  .directive('formEntry', function () {
    return {
      scope: {
        label: '@'
      },
      transclude: true,
      require: 'label',
      template: '<div class="form-group">\n    <label class="col-sm-2 control-label">{{label}}</label>\n    <div class="col-sm-10" ng-transclude></div>\n</div>',
      restrict: 'EA'
//      link: function postLink(scope, element, attrs) {
//        console.log(scope.readOnly, typeof scope.readOnly);
//      }
    };
  });
