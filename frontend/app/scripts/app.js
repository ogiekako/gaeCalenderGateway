'use strict';

angular.module('frontendApp', [
    'ngAnimate',
    'ngRoute',
    'ngSanitize',
    'ui.bootstrap'
  ])
  .config([
    '$provide', '$routeProvider',
    function ($provide, $routeProvider) {

      var ensureCredentials = {
        check: [
          '$q', 'BaseService', 'GlobalError', 'User', 'AppInfo',
          function ($q, BaseService, GlobalError, User, AppInfo) {
            if (User.authValid) {
              return;
            }

            var defer = $q.defer();

            BaseService.checkCredentials().then(
              function ok(result) {
                if (!result.valid) {
                  GlobalError.show('Invalid auth. <a href="/login">Please login</a>.');
                  defer.reject();
                  return;
                }
                User.authValid = true;
                User.userId = result.currentUserId;
                AppInfo.version = result.version;
                AppInfo.id = result.appId;
                console.log('setting AppInfo', AppInfo);
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

angular.module('frontendApp').run(
  function ($rootScope, Utils) {
    // define this helpful function on the root scope to get used in every .html file.
    $rootScope.emptyStr = Utils.emptyStr;
  }
);
