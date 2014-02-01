'use strict';

angular.module('frontendApp').service('DebugService',
  function ($http, Utils, Config) {

    /**
     *
     * @param {Date} begin
     * @param {Date} end
     * @returns {Promise}
     */
    this.getEvents = function (begin, end) {
      return Utils.handleResponse(
        $http({
          method: 'GET',
          url: Config.endpointUrl + 'debug/listEvents',
          params: {
            begin: begin.getTime(),
            end: end.getTime()
          }
        }),
        'Error getting event list: ');
    };
  }
);
