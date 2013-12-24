'use strict';

angular.module('frontendApp').service('BaseService', [
  '$http', '$q', 'Config',
  function ($http, $q, Config) {

    this.checkCredentials = function () {
      var defer = $q.defer();

      $http({method: 'GET', url: Config.endpointUrl + 'base/credentials'}).
        success(function (data, status, headers, config) {
          defer.resolve(data);
        }).
        error(function (data, status, headers, config) {
          defer.reject('Error checking credential status: ' + (data.msg || data));
        });

      return defer.promise;
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
  }
]);
