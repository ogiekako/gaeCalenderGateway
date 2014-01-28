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
          if (response.status >= 300 && response.status < 400) {
            var data = response.data;
            $log.info('Got non 3xx response from credentials test. Most likely you have to login again.', (data.msg || data));
            // return a resolved promise, so you just have to check the valid flag
            return $q.when({});
          }
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
