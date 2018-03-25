
from __future__ import print_function
import httplib2
import os

import random

from apiclient import discovery
from oauth2client import client
from oauth2client import tools
from oauth2client.file import Storage

##from miflora.miflora_poller import MiFloraPoller
##from miflora.backends.bluepy import BluepyBackend

import time
import schedule

try:
    import argparse
    flags = argparse.ArgumentParser(parents=[tools.argparser]).parse_args()
except ImportError:
    flags = None

# If modifying these scopes, delete your previously saved credentials
# at ~/.credentials/sheets.googleapis.com-python-quickstart.json
SCOPES = 'https://www.googleapis.com/auth/spreadsheets'
CLIENT_SECRET_FILE = 'client_secret.json'
APPLICATION_NAME = 'Google Sheets API Python Quickstart'

spreadsheetId = '1u-necJvOJWzQOEuCGBO0IvFBpVuVDzXJMtnHyXiEXS0'

MI_TEMP = "temp"
MI_LIGHT = "light"
MI_WATER = "water"
MI_COND = "cond"
run = 0

def job():
    schedule.every().hour.do(poll)
    poll()
    run = 1;  

def poll():
    print('mini working')
    out = dict()
    
##    poller = MiFloraPoller('C4:7C:8D:65:C9:B1', BluepyBackend)
##    temp = poller.parameter_value('temperature')
##    water = poller.parameter_value('moisture')
##    light = poller.parameter_value('light')
##    cond = poller.parameter_value('conductivity')
##    read_time = time.time()

    read_time = time.time()
    water = random.randint(0,100)   
    light = random.randint(0,100000)
    temp = random.uniform(24,27)
    cond = random.randint(0,1500)

    values = [
        [
            read_time,
            light,
            water,
            temp,
            cond
        ]
    ]
    body = {
        'values': values
    }
    rangeName = 'Sheet1!A1:E1'
    value_input_option = 'RAW'
    insert_data_option = 'INSERT_ROWS'
    result = service.spreadsheets().values().append(
        spreadsheetId=spreadsheetId, range=rangeName,
        valueInputOption=value_input_option,
        insertDataOption=insert_data_option,
        body=body).execute()
    print('{0} cells updated.'.format(result.get('updates').get('updatedCells')));


def get_credentials():
    """Gets valid user credentials from storage.

    If nothing has been stored, or if the stored credentials are invalid,
    the OAuth2 flow is completed to obtain the new credentials.

    Returns:
        Credentials, the obtained credential.
    """
    home_dir = os.path.expanduser('~')
    credential_dir = os.path.join(home_dir, '.credentials')
    if not os.path.exists(credential_dir):
        os.makedirs(credential_dir)
    credential_path = os.path.join(credential_dir,
                                   'sheets.googleapis.com-python-quickstart.json')

    store = Storage(credential_path)
    credentials = store.get()
    if not credentials or credentials.invalid:
        flow = client.flow_from_clientsecrets(CLIENT_SECRET_FILE, SCOPES)
        flow.user_agent = APPLICATION_NAME
        if flags:
            credentials = tools.run_flow(flow, store, flags)
        else: # Needed only for compatibility with Python 2.6
            credentials = tools.run(flow, store)
        print('Storing credentials to ' + credential_path)
    return credentials

def main():
    credentials = get_credentials()
    global http
    http = credentials.authorize(httplib2.Http())
    discoveryUrl = ('https://sheets.googleapis.com/$discovery/rest?'
                'version=v4')
    global service
    service = discovery.build('sheets', 'v4', http=http,
                          discoveryServiceUrl=discoveryUrl)

    schedule.every().day.at('12:17').do(job)

    # Polling
    while True:
        schedule.run_pending()
        if run == 1:
            break;
        time.sleep(1)

if __name__ == '__main__':
    main()
