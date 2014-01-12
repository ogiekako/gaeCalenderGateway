'use strict';

/**
 * Sets the active class on the li element based on the ng-href on the link.
 *
 * @see https://ryankaskel.com/blog/2013/05/27/a-different-approach-to-angularjs-navigation-menus
 */
angular.module('frontendApp').directive('navMenu',
  function ($log, $location) {
    return function (scope, element, attrs) {
      var listItems = element.find('li'),
        onClass = attrs.navMenu || 'on',
        routePattern,
        item,
        link,
        url,
        currentLink,
        urlMap = {},
        i;

      if (!$location.$$html5) {
        routePattern = /^#[^/]*/;
      }

      for (i = 0; i < listItems.length; i++) {
        item = angular.element(listItems[i]);
        link = item.find('a');
        url = link.attr('ng-href') || link.attr('href');
        if (!url) {
          $log.warn('no href or ng-href found.');
          continue;
        }

        if ($location.$$html5) {
          urlMap[url] = item;
        } else {
          urlMap[url.replace(routePattern, '')] = item;
        }
      }

      scope.$on('$routeChangeStart', function () {
        var pathLink = urlMap[$location.path()];

        if (pathLink) {
          if (currentLink) {
            currentLink.removeClass(onClass);
          }
          currentLink = pathLink;
          currentLink.addClass(onClass);
        }
      });
    };
  });

