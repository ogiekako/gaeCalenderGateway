'use strict';

angular.module('frontendApp').service('DebugService',
  function ($http, Utils, Config) {

    this.getEvents = function (sinceTs) {
      return Utils.handleResponse(
        $http({
          method: 'GET',
          url: Config.endpointUrl + 'debug/listEvents',
          params: { since: sinceTs }
        }),
        'Error getting event list: ');
    };
  }
);
