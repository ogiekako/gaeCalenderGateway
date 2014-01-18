'use strict';

angular.module('frontendApp').service('RawMailInService',
  function ($http, $q, Config, Utils) {

    this.getList = function () {
      var defer = $q.defer();

      $http({method: 'GET', url: Config.endpointUrl + 'rawMailIn/list'}).
        success(function (data, status, headers, config) {
          defer.resolve(data);
        }).
        error(function (data, status, headers, config) {
          defer.reject('Error getting raw mail list: ' + (data.msg || data));
        });

      return defer.promise;
    };

    this.getItem = function (itemId) {
      var defer = $q.defer();

      $http({method: 'GET', url: Config.endpointUrl + 'rawMailIn/item/' + itemId}).
        success(function (data, status, headers, config) {
          defer.resolve(data);
        }).
        error(function (data, status, headers, config) {
          defer.reject('Error getting raw mail item: ' + (data.msg || data));
        });

      return defer.promise;
    };

    this.updateItem = function (item) {
      var defer = $q.defer();

      $http({
        method: 'PUT',
        url: Config.endpointUrl + 'rawMailIn/item/' + item.id,
        data: item
      }).
        success(function (data, status, headers, config) {
          defer.resolve(data);
        }).
        error(function (data, status, headers, config) {
          defer.reject('Error updating raw mail: ' + (data.msg || data));
        });

      return defer.promise;
    };
  });
