'use strict';

angular.module('frontendApp').service('IcalInfoService',
  function ($http, $q, Config, Utils) {

    this.getList = function () {
      var defer = $q.defer();

      $http({method: 'GET', url: Config.endpointUrl + 'ical/list'}).
        success(function (data, status, headers, config) {
          defer.resolve(data);
        }).
        error(function (data, status, headers, config) {
          defer.reject('Error getting ical list: ' + (data.msg || data));
        });

      return defer.promise;
    };

    this.getItem = function (itemId) {
      var defer = $q.defer();

      $http({method: 'GET', url: Config.endpointUrl + 'ical/item/' + itemId}).
        success(function (data, status, headers, config) {
          defer.resolve(data);
        }).
        error(function (data, status, headers, config) {
          defer.reject('Error getting ical item: ' + (data.msg || data));
        });

      return defer.promise;
    };

    this.updateItem = function (item) {
      var defer = $q.defer();

      $http({
        method: 'PUT',
        url: Config.endpointUrl + 'ical/item/' + item.id,
        data: item
      }).
        success(function (data, status, headers, config) {
          defer.resolve(data);
        }).
        error(function (data, status, headers, config) {
          defer.reject('Error updating icals: ' + (data.msg || data));
        });

      return defer.promise;
    };

    this.addToCalendar = function (idList) {
      return Utils.handleResponse(
        $http({
          method: 'PUT',
          url: Config.endpointUrl + 'ical/addToCalendar',
          data: idList
        }),
        'Error adding items to calendar: ');
    };

    this.deleteItem = function (id) {
      return Utils.handleResponse(
        $http({
          method: 'DELETE',
          url: Config.endpointUrl + 'ical/item/' + id
        }),
        'Error deleting ical item: ');
    };

  });
