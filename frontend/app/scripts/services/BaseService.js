'use strict';

angular.module('frontendApp').service('BaseService',
  function ($http, $q, $log, Config, Utils) {

    this.ping = function () {
      return Utils.handleResponse(
        $http({
          method: 'GET',
          url: Config.endpointUrl + 'base/ping'
        }),
        'Error ping: ');
    };

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
          return $q.reject(response.data);
        });
    };


    this.listCalendars = function () {
      return Utils.handleResponse(
        $http({
          method: 'GET',
          url: Config.endpointUrl + 'base/calendars'
        }),
        'Error listing calendars: ');
    };
  });
