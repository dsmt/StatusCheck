$(function() {
  $('#submit').click(function () {
    $.post( '/status', { id: $('#id').val(), model: $('#model').val(),  rma: $('#rma').val() })
    .done(function( payload ) {
      //console.log( payload );

      $('#results').empty();
      var data = JSON.parse(payload);

      data.history.forEach(function(element) {
        $('#results').append(
        '<tr>' +
        '<td>' + element.status + '</td>' +
        '<td>' + element.rma + '</td>' +
        '<td>' + element.model + '</td>' +
        '<td>' + element.id + '</td>' +
        '<td>' + element.customer + '</td>' +
        '<td>' + element.engineer + '</td>' +
        '<td>' + element.work_started + '</td>' +
        '<td>' + element.service + '</td>' +
        '<td>' + element.part_no + '</td>' +
        '<td>' + element.part_ordered + '</td>' +
        '<td>' + element.work_ended + '</td>' +
        '</tr>');
        //console.log(element);
      });
    });
  });

  $('.date').each( function () {
    var dt = eval($(this).html());
    if (dt > 0) $(this).html(new Date(dt).toLocaleString());
    else $(this).html("-");
  });

  $(document).ready(function() {
    $('.select-model').select2({
        theme: 'bootstrap4',
        placeholder: "Серийный номер",
        //allowClear: true
    });
  });
});