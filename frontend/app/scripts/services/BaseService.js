'use strict';

angular.module('frontendApp').service('BaseService',
  function ($http, $q, $log, Config, Utils) {

    this.getStats = function () {
      return Utils.handleResponse(
        $http({
          method: 'GET',
          url: Config.endpointUrl + 'base/stats'
        }),
        'Error getting stats: ');
    };

    this.checkCredentials = function () {
      return $http({method: 'GET', url: Config.endpointUrl + 'base/credentials'}).then(
        function ok(response) {
          return response.data;
        },
        function error(response) {
          var data = response.data;
          $log.info('Got non 2xx response from credentials test. Most likely you have to login again.', (data.msg || data));
          return $q.when({});
        });
    };


    this.getConfig = function () {
      var defer = $q.defer();

      $http({method: 'GET', url: Config.endpointUrl + 'base/config'}).
        success(function (data, status, headers, config) {
          defer.resolve(data);
        }).
        error(function (data, status, headers, config) {
          defer.reject('Error loading the config: ' + data.msg || data);
        });

      return defer.promise;
    };

    this.listCalendars = function () {
      var defer = $q.defer();

      $http({method: 'GET', url: Config.endpointUrl + 'base/calendars'}).
        success(function (data, status, headers, config) {
          defer.resolve(data);
        }).
        error(function (data, status, headers, config) {
          defer.reject('Error listing calendars: ' + data.msg || data);
        });

      return defer.promise;

    };

    this.setCalendar = function (calendarId) {
      var defer = $q.defer();

      $http(
        {
          method: 'PUT',
          url: Config.endpointUrl + 'base/config/calendar',
          data: {
            id: calendarId
          }
        }).
        success(function (data, status, headers, config) {
          defer.resolve(data);
        }).
        error(function (data, status, headers, config) {
          defer.reject('Error setting calendarId: ' + data.msg || data);
        });

      return defer.promise;

    };
  });
