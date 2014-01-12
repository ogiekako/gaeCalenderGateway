'use strict';

angular.module('frontendApp').service('Utils',
  function Utils($q, GlobalError) {
    this.handleError = function (msg) {
      GlobalError.show(msg);
    };

    this.handleResponse = function (httpPromise, errorMsg) {
      var defer = $q.defer();

      httpPromise.success(function (data, status, headers, config) {
        defer.resolve(data);
      }).
        error(function (data, status, headers, config) {
          defer.reject(errorMsg + (data.msg || data));
        });

      return defer.promise;
    };

    this.shorter = function (text, maxLen) {
      if (typeof text !== 'string') {
        return '';
      }
      if (text.length <= maxLen - 3) {
        return text;
      }
      return text.substring(0, maxLen - 3) + '...';
    };
  });
