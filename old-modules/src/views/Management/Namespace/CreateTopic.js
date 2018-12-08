/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom'
import {
  Badge,
  Button,
  Card,
  CardBody,
  CardFooter,
  CardHeader,
  Col,
  Collapse,
  Fade,
  Form,
  FormGroup,
  FormText,
  Input,
  Label,
  Row,
  Table
} from 'reactstrap';
import { AppSwitch } from '@coreui/react'

import API from '../../../api'
import PULSAR from '../../../utils'

class CreateTopic extends Component {

  constructor(props) {
    super(props);
    this.tenant = this.props.match.params.tenant;
    this.namespace = this.props.match.params.namespace;
    this.state = {
      topic: ''
    };
    this.handleNameChange = this.handleNameChange.bind(this);
    this.handleCreateTopic = this.handleCreateTopic.bind(this);
  }

  handleNameChange(event) {
    this.setState({
      topic: event.target.value,
    });
  }

  handleCreateTopic(event) {
    event.preventDefault();
    this.props.history.push(PULSAR.getNamespaceUrl(this.tenant, this.namespace));
  }

  render() {
    return (
      <div className="animated fadeIn">
        <Row>
          <Col>
            <Card>
              <CardHeader>
                <strong>Create Topic for Namespace <Link to={PULSAR.getTenantUrl(this.tenant)}>
                {this.tenant}
                </Link>/<Link to={PULSAR.getNamespaceUrl(this.tenant, this.namespace)}>
                {this.namespace}
                </Link>
                </strong>
              </CardHeader>
              <CardBody>
                <Form onSubmit={this.handleCreateTopic} method="post" className="form-horizontal">
                  <FormGroup row>
                    <Col md="3">
                      <Label htmlFor="name">Name</Label>
                    </Col>
                    <Col xs="12" md="9">
                      <Input
                        type="text"
                        id="name"
                        value={this.state.topic}
                        onChange={this.handleNameChange}
                        placeholder="Topic name"
                        required/>
                      <FormText className="help-block">Please enter topic name</FormText>
                    </Col>
                  </FormGroup>
                  <FormGroup>
                    <Button type="submit" size="sm" color="primary">
                      <i className="fa fa-dot-circle-o"></i> Create
                    </Button>
                    <Link to={PULSAR.getNamespaceUrl(this.tenant, this.namespace)}>
                      <Button type="reset" size="sm" color="danger">
                        <i className="fa fa-ban"></i> Cancel
                      </Button>
                    </Link>
                  </FormGroup>
                </Form>
              </CardBody>
            </Card>
          </Col>
        </Row>
      </div>
    );
  }

}

export default withRouter(CreateTopic);
