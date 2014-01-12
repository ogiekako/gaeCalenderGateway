'use strict';

angular.module('frontendApp', [
    'ngAnimate',
    'ngRoute',
    'ngSanitize'
  ])
  .config([
    '$provide', '$routeProvider',
    function ($provide, $routeProvider) {

      var ensureCredentials = {
        check: [
          '$q', 'BaseService', 'GlobalError', 'User',
          function (a, BaseService, GlobalError, User) {
            if (User.authValid) {
              return;
            }

            console.log('checking');
            var defer = a.defer();

            BaseService.checkCredentials().then(
              function ok(result) {
                if (!result.valid) {
                  GlobalError.show('Invalid auth. <a href="/login">Please login</a>.');
                  defer.reject();
                  return;
                }
                User.authValid = true;
                User.userId = result.currentUserId;
                defer.resolve();
              },
              function fail(msg) {
                GlobalError.show('Error checking authentification. ' + msg);
                defer.reject();
              });

            return defer.promise;
          }
        ]
      };

      $routeProvider
        .when('/', {
          templateUrl: 'views/main.html',
          controller: 'MainCtrl',
          resolve: ensureCredentials
        })
        .when('/config', {
          templateUrl: 'views/config.html',
          controller: 'ConfigCtrl',
          resolve: ensureCredentials
        })
        .when('/rawMailIn', {
          templateUrl: 'views/rawMailIn.html',
          controller: 'RawMailInCtrl',
          resolve: ensureCredentials
        })
        .when('/rawMailIn/:mailId', {
          templateUrl: 'views/rawMailInDetail.html',
          controller: 'RawMailInDetailCtrl',
          resolve: ensureCredentials
        })
        .when('/ical', {
          templateUrl: 'views/ical.html',
          controller: 'IcalInfoCtrl',
          resolve: ensureCredentials
        })
        .when('/ical/:id', {
          templateUrl: 'views/icalDetail.html',
          controller: 'IcalInfoDetailCtrl',
          resolve: ensureCredentials
        })
        .when('/debug', {
          templateUrl: 'views/debug.html',
          controller: 'DebugCtrl',
          resolve: ensureCredentials
        })
        .otherwise({
          redirectTo: '/'
        });
    }
  ]);
//  .run([
//    '$rootScope', 'GlobalError',
//    function ($rootScope, GlobalError) {
//      $rootScope.$on('$routeChangeError', function(ngEvent, current, prev, rejection) {
//        console.log(ngEvent, current, prev, rejection);
//      });
//    }
//  ]);
