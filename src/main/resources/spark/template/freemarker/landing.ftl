<!doctype html>
<html lang="en">
  <head>
    <!-- Required meta tags -->
    <link href="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.7/css/select2.min.css" rel="stylesheet" />
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css" integrity="sha384-9gVQ4dYFwwWSjIDZnLEWnxCjeSWFphJiwGPXr1jddIhOegiu1FwO5qRGvFXOdJZ4" crossorigin="anonymous">
    <link href="/style/style.css?v=${version}" rel="stylesheet" type="text/css">
    <link href="/style/select2-bootstrap4.css" rel="stylesheet" type="text/css">

    <title>Service status</title>
  </head>
  <body>
    <div class="container">
      <div class="row form">
        <div class="col-2">
          <input type="text" class="form-control" id="rma" value="" placeholder="RMA">
        </div>
        <div class="col-1"><p class="text-center or">или</p></div>
        <div class="col-3">
          <select class="form-control select-model" id="model" data-placeholder="Модель">
            <#list models as model>
              <option></option>
              <option value="${model}">${model}</option>
            </#list>
          </select>
        </div>
        <div class="col-3">
          <input type="text" class="form-control" id="id" value="" placeholder="Серийный номер">
        </div>
        <div class="col-1"></div>
        <div class="col-2">
          <button id="submit" type="submit" class="btn btn-primary mb-2">Проверить статус</button>
        </div>
      </div>
      <div class="row">
        <div class="col">
        <table class="table small">
          <thead class="thead-light">
            <tr>
              <th scope="col">Статус</th>
              <th scope="col">RMA</th>
              <th scope="col">Модель</th>
              <th scope="col">Серийный номер</th>
              <th scope="col">Заказчик</th>
              <th scope="col">Ответственный</th>
              <th scope="col">Начало работы</th>
              <th scope="col">Услуги</th>
              <th scope="col">Номер запчасти </th>
              <th scope="col">Запчасть заказана</th>
              <th scope="col">Окончание работы</th>
            </tr>
          </thead>
          <tbody id="results">

          </tbody>
        </table>
      </div>
    </div>
    <!-- <div class="row">
      <div class="col small">
        <b>is loaded:</b> ${isLoaded?c}, <b>records:</b> ${records},
        <b>last update:</b> <span class="date">${lastUpdate?c}</span>, <b>last success</b> <span class="date">${lastSuccess?c}</span>
      </div>
    </div> -->
    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.0/jquery.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/js/bootstrap.min.js" integrity="sha384-uefMccjFJAIv6A+rW+L4AHf99KvxDjWSu1z9VI8SKNVmz4sk7buKt/6v9KI65qnm" crossorigin="anonymous"></script>
    <script src="/script/script.js?v=${version}"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.7/js/select2.min.js"></script>
  </body>
</html>