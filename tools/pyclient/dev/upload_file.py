"""
Script for developing the method that uploads a file to a server.

The script creates a new staging area, titled 'Upload Test' with the Pet store template.
Then the ZIP file containing 'pet store' demo data is uploaded.
Next, the new data from the tables on the schema are retrieved.
Finally, the schema is deleted from the EMX2 dev server.
"""
import logging
import os
from pathlib import Path

from dotenv import load_dotenv

from tools.pyclient.src.molgenis_emx2_pyclient import Client
from tools.pyclient.src.molgenis_emx2_pyclient.exceptions import (NoSuchSchemaException, GraphQLException,
                                                                  PermissionDeniedException, PyclientException)


def upload_file(file_name: str):
    # Set up the logger
    logging.basicConfig(level='INFO')
    logging.getLogger("requests").setLevel(logging.WARNING)
    logging.getLogger("urllib3").setLevel(logging.WARNING)

    # Load the login details into the environment
    load_dotenv()
    token = os.environ.get('MG_TOKEN')

    # Connect to server and create, update, and drop schemas
    with Client('https://emx2.dev.molgenis.org/', token=token) as client:

        # Download catalogue.zip
        client.export(schema='catalogue', fmt='csv')

        # Create a schema
        try:
            client.create_schema(name='Upload Test')
            print(client.schema_names)
        except (GraphQLException, PermissionDeniedException, PyclientException) as e:
            print(e)

        client.set_schema('Upload Test')

        file_path = Path(file_name).absolute()

        client.upload_file(file_path)

        try:
            print(client.get(table='Cohorts', schema='Upload Test', as_df=True).to_string())
        except PyclientException as e:
            print(e)

        # Delete the schema
        try:
            client.delete_schema(name='Upload Test')
            print(client.schema_names)
        except (GraphQLException, NoSuchSchemaException) as e:
            print(e)

        os.remove('catalogue.zip')


if __name__ == '__main__':
    upload_file(file_name='catalogue.zip')
