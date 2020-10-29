angular.module('WiseHands')
  .controller('SelectAdditionsController', ['$scope', '$http', 'sideNavInit', '$routeParams',
    function ($scope, $http, sideNavInit, $routeParams) {

      $http({
        method: 'GET',
        url: `/api/product/${$routeParams.productUuid}`
      }).then(response => {
          $scope.activeShop = localStorage.getItem('activeShop');
          $scope.product = response.data;
          _getAvailableAdditions();
        }, error => console.log(error)
      );

      function _getAvailableAdditions() {
        $http({
          method: 'GET',
          url: '/api/addition/list'
        }).then(response => _setAvailableAdditions(response.data, $scope.product.selectedAddition),
          error => console.log(error)
        );
      }

      function _setAvailableAdditions(availableAdditions, selectedAddition) {
        console.log("availableAdditions => ", availableAdditions);
        console.log("selectedAddition => ", selectedAddition);
        const parsedAdditions = [];
        availableAdditions.forEach(availableAddition => {
          const match = selectedAddition.find(item => item.addition.uuid === availableAddition.uuid);
          parsedAdditions.push(Object.assign(availableAddition, match));
        });
        $scope.availableAdditions = parsedAdditions;
      }

      $scope.selectedDefaultAddition = (event, {addition}) => {
        event.stopPropagation();
        addition.isSelected = addition.isDefault;
        const url = `/api/addition/set/default/${$routeParams.productUuid}/${addition.uuid}/${addition.isDefault}`;
        sentSelectedAddition(url, addition);
        console.log("sentSelectedAddition is default => ", url);
      };

      $scope.selectedAddition = ({addition}) => {
        addition.isSelected = !addition.isSelected;
        if (addition.isDefault) addition.isDefault = !addition.isDefault;
        const url = addition.isSelected ? `/api/addition/add/${$routeParams.productUuid}/${addition.uuid}` : `/api/addition/remove/${addition.uuid}`;
        sentSelectedAddition(url, addition);
      };

      let sentSelectedAddition = (url, addition) => {
        console.log("sentSelectedAddition is selected => ", url);
        $http({
          method: 'PUT',
          url: url
        }).then(response => addition.isSelected = response.data.isSelected,
            error => console.log(error)
        );
      };

      sideNavInit.sideNav();
    }]);


