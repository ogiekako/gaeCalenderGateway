'use strict';

angular.module('frontendApp').controller('MainCtrl',
  function ($scope, BaseService, GlobalError, Utils) {

    function handleError(msg) {
      GlobalError.show(msg);
      $scope.loading = false;
    }

    function boolCheck(value) {
      return value;
    }

    function numberCheck(value) {
      return value === 0;
    }

    $scope.checks = [
      { id: 'user_selected', label: 'User selected', link: '#/config', check: boolCheck},
      { id: 'calendar_selected', label: 'Calendar selected', link: '#/config', check: boolCheck},
      { id: 'contact_entered', label: 'Contact mail entered', link: '#/config', check: boolCheck},
      { id: 'rawMailIn_incoming', label: 'Waiting RawMails', link: '#/rawMailIn', check: numberCheck},
      { id: 'rawMailIn_errors', label: 'Error RawMails', link: '#/rawMailIn', check: numberCheck},
      { id: 'iCalInfo_parsed', label: '(Only) parsed iCalInfo', link: '#/ical', check: numberCheck },
      { id: 'iCalInfo_add_errors', label: 'Error iCalInfo', link: '#/ical', check: numberCheck }
    ];
    $scope.stats = {};
    $scope.loading = true;

    BaseService.getStats().then(
      function ok(stats) {
        $scope.stats = stats;
        $scope.loading = false;
      },
      handleError
    );

  });
