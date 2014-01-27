'use strict';

angular.module('frontendApp').service('ConfigService',
  function ($http, $q, $log, Config, Utils) {

    this.getConfig = function () {
      return Utils.handleResponse(
        $http({
          method: 'GET',
          url: Config.endpointUrl + 'config'
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
          url: Config.endpointUrl + 'config/contactEmail',
          data: {
            contactEmail: email
          }
        }),
        'Error setting contact email: ');
    };

    this.setCalendar = function (calendarId) {
      return Utils.handleResponse(
        $http({
          method: 'PUT',
          url: Config.endpointUrl + 'config/calendar',
          data: {
            id: calendarId
          }
        }),
        'Error setting calendarId: ');
    };

    this.testMailConfiguration = function () {
      return Utils.handleResponse(
        $http({
          method: 'GET',
          url: Config.endpointUrl + 'config/testMail'
        }),
        'Error testing mail configuration: ');
    };
  });
