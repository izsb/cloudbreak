{%- from 'metadata/settings.sls' import metadata with context %}
{%- from 'nodes/settings.sls' import host with context %}

/etc/dhcp/dhclient.d/google_hostname.sh:
  file.managed:
    - makedirs: True
    - source: salt://unbound/dhcp/google_hostname.sh
    - mode: 744

disable_unbound:
  service.disabled:
    - name: unbound

/etc/dhcp/dhclient-enter-hooks:
  file.managed:
    - contents: 'echo "that was fun!"'

/etc/resolv.conf:
  file.managed:
    - makedirs: True
    - source: salt://unbound/config/resolv.conf
    - template: jinja
    - context:
      private_address: {{ host.private_address }}
