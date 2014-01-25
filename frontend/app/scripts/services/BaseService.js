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
          // return a resolved promise, so you just have to check the valid flag
          return $q.when({});
        });
    };


    this.getConfig = function () {
      return Utils.handleResponse(
        $http({
          method: 'GET',
          url: Config.endpointUrl + 'base/config'
        }),
        'Error loading the config: ');
    };

    /**
     * @param {string} email
     * @returns {Promise}
     */
    this.setContactEmail = function (email) {
      return Utils.handleResponse(
        $http({
          method: 'PUT',
          url: Config.endpointUrl + 'base/config/contactEmail',
          data: {
            contactEmail: email
          }
        }),
        'Error setting contact email: ');
    };

    this.listCalendars = function () {
      return Utils.handleResponse(
        $http({
          method: 'GET',
          url: Config.endpointUrl + 'base/calendars'
        }),
        'Error listing calendars: ');
    };

    this.setCalendar = function (calendarId) {
      return Utils.handleResponse(
        $http({
          method: 'PUT',
          url: Config.endpointUrl + 'base/config/calendar',
          data: {
            id: calendarId
          }
        }),
        'Error setting calendarId: ');
    };
  });
